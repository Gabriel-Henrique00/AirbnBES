package br.ifsp.domain.shared.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Tag("Structural")
@Tag("UnitTest")
@DisplayName("Address Value Object Tests")
class AddressTest {

    private static final String DEFAULT_NUMBER = "123";
    private static final String DEFAULT_STREET = "Main St";
    private static final String DEFAULT_CITY = "Springfield";
    private static final String DEFAULT_STATE = "IL";
    private static final String DEFAULT_POSTAL_CODE = "62704";

    private Address createDefaultAddress() {
        return new Address(DEFAULT_NUMBER, DEFAULT_STREET, DEFAULT_CITY, DEFAULT_STATE, DEFAULT_POSTAL_CODE);
    }

    @Nested
    @DisplayName("Constructors and Getters")
    class ConstructorsAndGettersTests {

        @Test
        @DisplayName("Should create an Address with all arguments")
        void shouldCreateAddressWithAllArguments() {
            Address address = new Address(DEFAULT_NUMBER, DEFAULT_STREET, DEFAULT_CITY, DEFAULT_STATE, DEFAULT_POSTAL_CODE);

            assertThat(address).isNotNull();
            assertThat(address.getNumber()).isEqualTo(DEFAULT_NUMBER);
            assertThat(address.getStreet()).isEqualTo(DEFAULT_STREET);
            assertThat(address.getCity()).isEqualTo(DEFAULT_CITY);
            assertThat(address.getState()).isEqualTo(DEFAULT_STATE);
            assertThat(address.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        }
        @Test
        @DisplayName("Should create an Address using builder pattern")
        void shouldCreateAddressUsingBuilderPattern() {
            Address address = Address.builder()
                    .number(DEFAULT_NUMBER)
                    .street(DEFAULT_STREET)
                    .city(DEFAULT_CITY)
                    .state(DEFAULT_STATE)
                    .postalCode(DEFAULT_POSTAL_CODE)
                    .build();

            assertThat(address).isNotNull();
            assertThat(address.getNumber()).isEqualTo(DEFAULT_NUMBER);
            assertThat(address.getStreet()).isEqualTo(DEFAULT_STREET);
            assertThat(address.getCity()).isEqualTo(DEFAULT_CITY);
            assertThat(address.getState()).isEqualTo(DEFAULT_STATE);
            assertThat(address.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        }

        @Test
        @DisplayName("Should instantiate Address using no-args constructor (for JPA/Lombok)")
        void shouldInstantiateAddressUsingNoArgsConstructor() {
            try {
                Address address = Address.class.getDeclaredConstructor().newInstance();
                assertThat(address).isNotNull();
                // Asserts para campos nulos, pois @NonNull não é validado no construtor padrão
                assertThat(address.getNumber()).isNull();
                assertThat(address.getStreet()).isNull();
                assertThat(address.getCity()).isNull();
                assertThat(address.getState()).isNull();
                assertThat(address.getPostalCode()).isNull();
            } catch (Exception e) {
                fail("Failed to instantiate Address using no-args constructor: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Should throw NullPointerException when building Address with null number")
        void shouldThrowNullPointerExceptionWhenBuildingWithNullNumber() {
            assertThatThrownBy(() -> Address.builder()
                    .street(DEFAULT_STREET)
                    .city(DEFAULT_CITY)
                    .state(DEFAULT_STATE)
                    .postalCode(DEFAULT_POSTAL_CODE)
                    .build())
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("number is marked non-null but is null");
        }

        @Test
        @DisplayName("Should throw NullPointerException when building Address with null street")
        void shouldThrowNullPointerExceptionWhenBuildingWithNullStreet() {
            assertThatThrownBy(() -> Address.builder()
                    .number(DEFAULT_NUMBER)
                    .city(DEFAULT_CITY)
                    .state(DEFAULT_STATE)
                    .postalCode(DEFAULT_POSTAL_CODE)
                    .build())
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("street is marked non-null but is null");
        }

        @Test
        @DisplayName("Should throw NullPointerException when building Address with null city")
        void shouldThrowNullPointerExceptionWhenBuildingWithNullCity() {
            assertThatThrownBy(() -> Address.builder()
                    .number(DEFAULT_NUMBER)
                    .street(DEFAULT_STREET)
                    .state(DEFAULT_STATE)
                    .postalCode(DEFAULT_POSTAL_CODE)
                    .build())
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("city is marked non-null but is null");
        }
        @Test
        @DisplayName("Should throw NullPointerException when building Address with null state")
        void shouldThrowNullPointerExceptionWhenBuildingWithNullState() {
            assertThatThrownBy(() -> Address.builder()
                    .number(DEFAULT_NUMBER)
                    .street(DEFAULT_STREET)
                    .city(DEFAULT_CITY)
                    .postalCode(DEFAULT_POSTAL_CODE)
                    .build())
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("state is marked non-null but is null");
        }
        @Test
        @DisplayName("Should throw NullPointerException when building Address with null postalCode")
        void shouldThrowNullPointerExceptionWhenBuildingWithNullPostalCode() {
            assertThatThrownBy(() -> Address.builder()
                    .number(DEFAULT_NUMBER)
                    .street(DEFAULT_STREET)
                    .city(DEFAULT_CITY)
                    .state(DEFAULT_STATE)
                    .build())
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("postalCode is marked non-null but is null");
        }
    }

    @Nested
    @DisplayName("Equals Method Tests")
    class EqualsMethodTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            Address address = createDefaultAddress();
            assertThat(address).isEqualTo(address);
        }
        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            Address address = createDefaultAddress();
            assertThat(address).isNotEqualTo(null);
        }
        @Test
        @DisplayName("Should not be equal to a different type of object")
        void shouldNotBeEqualToDifferentTypeOfObject() {
            Address address = createDefaultAddress();
            assertThat(address).isNotEqualTo(new Object());
        }
        @Test
        @DisplayName("Should be equal to another Address with same values")
        void shouldBeEqualToAnotherAddressWithSameValues() {
            Address address1 = createDefaultAddress();
            Address address2 = createDefaultAddress();
            assertThat(address1).isEqualTo(address2);
        }
        @Test
        @DisplayName("Should not be equal if numbers are different")
        void shouldNotBeEqualIfNumbersAreDifferent() {
            Address address1 = createDefaultAddress();
            Address address2 = new Address("456", DEFAULT_STREET, DEFAULT_CITY, DEFAULT_STATE, DEFAULT_POSTAL_CODE);
            assertThat(address1).isNotEqualTo(address2);
        }
        @Test
        @DisplayName("Should not be equal if streets are different")
        void shouldNotBeEqualIfStreetsAreDifferent() {
            Address address1 = createDefaultAddress();
            Address address2 = new Address(DEFAULT_NUMBER, "New St", DEFAULT_CITY, DEFAULT_STATE, DEFAULT_POSTAL_CODE);
            assertThat(address1).isNotEqualTo(address2);
        }
        @Test
        @DisplayName("Should not be equal if cities are different")
        void shouldNotBeEqualIfCitiesAreDifferent() {
            Address address1 = createDefaultAddress();
            Address address2 = new Address(DEFAULT_NUMBER, DEFAULT_STREET, "Capital City", DEFAULT_STATE, DEFAULT_POSTAL_CODE);
            assertThat(address1).isNotEqualTo(address2);
        }
        @Test
        @DisplayName("Should not be equal if states are different")
        void shouldNotBeEqualIfStatesAreDifferent() {
            Address address1 = createDefaultAddress();
            Address address2 = new Address(DEFAULT_NUMBER, DEFAULT_STREET, DEFAULT_CITY, "CA", DEFAULT_POSTAL_CODE);
            assertThat(address1).isNotEqualTo(address2);
        }
        @Test
        @DisplayName("Should not be equal if postal codes are different")
        void shouldNotBeEqualIfPostalCodesAreDifferent() {
            Address address1 = createDefaultAddress();
            Address address2 = new Address(DEFAULT_NUMBER, DEFAULT_STREET, DEFAULT_CITY, DEFAULT_STATE, "90210");
            assertThat(address1).isNotEqualTo(address2);
        }
    }
    @Nested
    @DisplayName("HashCode Method Tests")
    class HashCodeMethodTests {

        @Test
        @DisplayName("Should return same hash code for equal objects")
        void shouldReturnSameHashCodeForEqualObjects() {
            Address address1 = createDefaultAddress();
            Address address2 = createDefaultAddress();
            assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
        }
        @Test
        @DisplayName("Should return different hash code for different objects")
        void shouldReturnDifferentHashCodeForDifferentObjects() {
            Address address1 = createDefaultAddress();
            Address address2 = new Address("456", DEFAULT_STREET, DEFAULT_CITY, DEFAULT_STATE, DEFAULT_POSTAL_CODE);
            assertThat(address1.hashCode()).isNotEqualTo(address2.hashCode());
        }
    }
    @Nested
    @DisplayName("ToString Method Tests")
    class ToStringMethodTests {

        @Test
        @DisplayName("Should return the formatted string representation")
        void shouldReturnFormattedStringRepresentation() {
            Address address = createDefaultAddress();
            String expectedString = String.format("%s, %s - %s, %s, %s", DEFAULT_STREET, DEFAULT_NUMBER, DEFAULT_CITY, DEFAULT_STATE, DEFAULT_POSTAL_CODE);
            assertThat(address.toString()).isEqualTo(expectedString);
        }
    }


    @Test
    @Tag("“Mutation”")
    @Tag("“UnitTest”")
    @DisplayName("Builder toString should include class name and fields")
    void builderToStringShouldIncludeClassNameAndFields() {
        String toString = Address.builder().toString();
        assertThat(toString)
                .startsWith("Address.AddressBuilder(")
                .contains("number=")
                .contains("street=")
                .contains("city=")
                .contains("state=")
                .contains("postalCode=");
    }
}
