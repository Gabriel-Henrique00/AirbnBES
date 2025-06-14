package br.ifsp.application.rental.update.owner;

import br.ifsp.application.rental.repository.JpaRentalRepository;
import br.ifsp.application.rental.repository.RentalMapper;
import br.ifsp.application.shared.exceptions.EntityNotFoundException;
import br.ifsp.application.shared.presenter.PreconditionChecker;
import br.ifsp.domain.models.rental.Rental;
import br.ifsp.domain.models.rental.RentalEntity;
import br.ifsp.domain.models.rental.RentalState;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OwnerUpdateRentalService implements IOwnerUpdateRentalService {

    private final JpaRentalRepository rentalRepository;
    private final Clock clock;

    public OwnerUpdateRentalService(JpaRentalRepository rentalRepository, Clock clock) {
        this.rentalRepository = rentalRepository;
        this.clock = clock;
    }

    @Override
    public void confirmRental(OwnerUpdateRentalPresenter presenter, RequestModel request) {
        try {
            Rental rental = getRental(request).orElseThrow(() -> new EntityNotFoundException("Rental not found"));

            if (!rental.getProperty().getOwner().getId().equals(request.ownerId())) {
                throw new SecurityException("Only the property owner can confirm the rental.");
            }

            if (!rental.getState().equals(RentalState.PENDING)) {
                throw new UnsupportedOperationException("Rental must be in a PENDING state to be confirmed.");
            }

            RentalEntity rentalEntity = RentalMapper.toEntity(rental);
            var conflicts = rentalRepository.findRentalsByOverlapAndState(
                    rentalEntity.getPropertyEntity().getId(),
                    RentalState.CONFIRMED,
                    rentalEntity.getStartDate(),
                    rentalEntity.getEndDate(),
                    rentalEntity.getId()
            );

            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Cannot confirm rental due to conflict with another confirmed rental.");
            }

            rental.setState(RentalState.CONFIRMED);
            rentalRepository.save(RentalMapper.toEntity(rental));
            restrainPendingRentalsInConflict(RentalMapper.toEntity(rental));

            presenter.prepareSuccessView(new ResponseModel(request.ownerId(), rentalEntity.getUserEntity().getId()));
        } catch (Exception e) {
            presenter.prepareFailView(e);
        }
    }

    @Override
    public void denyRental(OwnerUpdateRentalPresenter presenter, RequestModel request) {
        try {
            Rental rental = getRental(request).orElseThrow(() -> new EntityNotFoundException("Rental not found"));

            if (!rental.getProperty().getOwner().getId().equals(request.ownerId())) {
                throw new SecurityException("Only the property owner can deny the rental.");
            }

            if (!List.of(RentalState.PENDING, RentalState.RESTRAINED).contains(rental.getState())) {
                throw new UnsupportedOperationException(
                        String.format("Cannot deny a rental that is %s.", rental.getState())
                );
            }

            rental.setState(RentalState.DENIED);
            rentalRepository.save(RentalMapper.toEntity(rental));

            presenter.prepareSuccessView(new ResponseModel(request.ownerId(), rental.getUser().getId()));
        } catch (Exception e) {
            presenter.prepareFailView(e);
        }
    }

    @Override
    public void cancelRental(OwnerUpdateRentalPresenter presenter, RequestModel request, LocalDate cancelDate) {
        try {
            Rental rental = getRental(request).orElseThrow(() -> new EntityNotFoundException("Rental not found"));

            if (!rental.getProperty().getOwner().getId().equals(request.ownerId())) {
                throw new SecurityException("Only the property owner can cancel the rental.");
            }

            if (cancelDate == null) cancelDate = LocalDate.now(clock);
            PreconditionChecker.prepareIfTheDateIsInThePast(presenter, clock, cancelDate);

            if (!rental.getState().equals(RentalState.CONFIRMED)) {
                throw new IllegalArgumentException("Only confirmed rentals can be cancelled.");
            }

            rental.setState(RentalState.CANCELLED);

            List<Rental> restrainedConflicts = findRestrainedConflictingRentals(RentalMapper.toEntity(rental));
            restrainedConflicts.forEach(r -> r.setState(RentalState.PENDING));

            rentalRepository.save(RentalMapper.toEntity(rental));
            rentalRepository.saveAll(
                    restrainedConflicts.stream()
                            .map(RentalMapper::toEntity)
                            .collect(Collectors.toList())
            );

            presenter.prepareSuccessView(new ResponseModel(request.ownerId(), rental.getUser().getId()));
        } catch (Exception e) {
            presenter.prepareFailView(e);
        }
    }

    public void restrainPendingRentalsInConflict(RentalEntity confirmedRentalEntity) {
        List<RentalEntity> pendingConflicts = rentalRepository.findRentalsByOverlapAndState(
                confirmedRentalEntity.getPropertyEntity().getId(),
                RentalState.PENDING,
                confirmedRentalEntity.getStartDate(),
                confirmedRentalEntity.getEndDate(),
                confirmedRentalEntity.getId()
        );

        pendingConflicts.forEach(r -> {

            var rental = RentalMapper.toDomain(r, clock);

            if (rental.getState() != RentalState.EXPIRED) {
                rental.setState(RentalState.RESTRAINED);
            }

            rentalRepository.save(RentalMapper.toEntity(rental));
        });
    }

    private List<Rental> findRestrainedConflictingRentals(RentalEntity rentalEntity) {
        return rentalRepository.findRentalsByOverlapAndState(
                rentalEntity.getPropertyEntity().getId(),
                RentalState.RESTRAINED,
                rentalEntity.getStartDate(),
                rentalEntity.getEndDate(),
                rentalEntity.getId()
        ).stream().map(r -> RentalMapper.toDomain(r, clock)).collect(Collectors.toList());
    }

    private Optional<Rental> getRental(RequestModel requestModel) {
        RentalEntity rentalEntity = rentalRepository.findById(requestModel.rentalId()).orElse(null);

        return rentalEntity != null ? Optional.of(RentalMapper.toDomain(rentalEntity, clock)) : Optional.empty();
    }
}
