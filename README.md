# ValidationBuilder & ValidationResult

## Purpose

A simple builder to allow a bean instance to have a valid state without constructors containing validation logic.
This is done using a builder which initializes the bean, validates it, then returning it.

This will simplify reading of code and will prevent compile time changes when editing complex constructors.
A valid instance that is supposed to be interacted with without using mutability-disadvantages of setters.
In order to change the state of the bean, it should be done in "intelligent" methods.

This will also simplify unit testing as it is possible to turn off validation during testing allowing to only test with
the data needed for the test.

### Purpose of the validation result

The validation result is a loosely coupled bean and can be used without the builder. If one which to incorporate
some validation logic in some bean, then this bean can be used and simple suppressing of its result could
be done with a `@ExtendWith(ValidationResultExtension.class)`

## Advantages of use

* simpler code to read and maintain
* easier maintenance
* immutable beans or beans only mutable through methods (no setters need to be public and no public constructor)
* simple unit testing of validation for valid instances
* turn of validation in unit testing to provide instances with data only needed for the test

## Usages of the Builder

### Test of `NamedEraBuilder` (sample of a builder implementation)

    @DisplayName("should not build NamedEra without a naming it")
    @Test void shouldNotInitNamedEraWithoutName() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder(NamedEra.validate())::build)
                .withMessageContaining("must be named");
    }

    @DisplayName("should not build NamedEra without a beginning")
    @Test void shouldNotInitNamedEraWithoutBeginning() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder(NamedEra.validate()).withName("An era")::build)
                .withMessageContaining("must have a beginning");
    }

    @DisplayName("should not build NamedEra when the end date is before the beginning")
    @Test void shouldNotInitNamedAreatWithBeginningAfterTheEnd() {
        assertThatIllegalStateException().isThrownBy(() ->
                new NamedEraBuilder(NamedEra.validate())
                        .withName("The era")
                        .withBeginning(LocalDate.now())
                        .withEnd(LocalDate.now().minusDays(1))
                        .build()
        ).withMessageContaining("'end' cannot come before the beginning");
    }

    @DisplayName("should not build NamedEra when the end date is equal to the beginning")
    @Test void shouldNotInitNamedAreatWithBeginningEqualToTheEnd() {
        assertThatIllegalStateException().isThrownBy(() ->
                new NamedEraBuilder(NamedEra.validate())
                        .withName("The era")
                        .withBeginning(LocalDate.now())
                        .withEnd(LocalDate.now())
                        .build()
        ).withMessageContaining("'end' cannot be equal to the beginning");
    }

    @DisplayName("should build a NamedEra when all required ")
    @Test void shouldInitValidInstanceOnlyApplyingNameAndBeginning() {
        assertThat(
                new NamedEraBuilder(NamedEra.validate()).withName("An era").withBeginning(LocalDate.now()).build()
        ).isNotNull();

## Test of `NamedEra` (sample of a bean using a builder)

    @DisplayName("should calculate number of months in an era")
    @ExtendWith(ValidationResultExtension.class)
    @Test void shouldCalculateNoOfMonthsInAnEra() {
        NamedEra namedEra = NamedEra.aNamedEra()
                .withBeginning(LocalDate.now().minusYears(3))
                .withEnd(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(24L);
    }

    @DisplayName("should calculate length of era against todays date when no end date is not specified")
    @ExtendWith(ValidationResultExtension.class)
    @Test void shouldCalculateEraLenghtUsingToDaysDateWhenNoEndDateIsSpecified() {
        NamedEra namedEra = NamedEra.aNamedEra()
                .withBeginning(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(12L);
    }


### Test of `AbstractBuilder`

    @DisplayName("should build a bean")
    @Test void shouldBuildBean() {
        builder = new AbstractBuilder<Bean>(b -> Optional.empty()) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        Bean bean = builder.build();

        assertThat(bean).isNotNull();
    }

    @DisplayName("should fail the build when the instance is not valid")
    @Test void shouldFailValidationOfBean() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class).notNull("nullField", null, "cannot be null"))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'nullField' cannot be null");
    }

    @DisplayName("should fail the build when the instance is not valid and provide the names of all invalid fields")
    @Test void shouldFailValidationOfBeanWithMessageContainingAllTheInvalidFields() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class)
                        .notNull("aField", null, "cannot be null")
                        .notNull("anotherField", null, "cannot be null")
                )
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'aField' cannot be null,\n- 'anotherField' cannot be null");
    }

    @DisplayName("should fail the build when a string is empty")
    @Test void shouldFailValidationOfBeanWhenStringIsEmpty() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class).notEmpty("emptyField", "", "cannot be empty"))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'emptyField' cannot be empty");
    }

    @DisplayName("should fail the build when a condition is true")
    @Test void shouldFailValidationOfBeanWhenConditionIsTrue() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class).notTrue("fieldName", () -> true, "validation cannot be true"))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'fieldName' validation cannot be true");
    }

    @DisplayName("should fail the build when a condition is false")
    @Test void shouldFailValidationOfBeanWhenConditionIsFalse() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(validate(Bean.class).notFalse("fieldName", () -> false, "validation cannot be false"))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Bean has invalid fields:\n- 'fieldName' validation cannot be false");
    }

    private class Bean {
    }

###  Test of `ValidationResultExtension`

    @ExtendWith(ValidationResultExtension.class)
    @DisplayName("used only as an declarative extension")
    @Nested class DeclarativeUse {

        @DisplayName("should suppress all build validations when no specification is given")
        @Test void shouldSuppressBuildValidations() {
            assertAll(
                    () -> assertThat(new InvalidBeanBuilder().build()).as("invalid bean").isNotNull(),
                    () -> assertThat(new AnotherInvalidBeanBuilder().build()).as("another invalid bean").isNotNull()
            );
        }
    }

    @ExtendWith(ValidationResultExtension.class)
    @DisplayName("used as an declarative, but programmatically extension")
    @Nested class ProgrammaticallyUse {

        @DisplayName("should suppress a validation for given class only")
        @Test void shouldSuppressBuildValidationOnlyForGivenClass() {
            ValidationResultExtension.suppressValidationFor(InvalidBean.class);

            assertAll(
                    () -> assertThat(new InvalidBeanBuilder().build()).as("invalid bean").isNotNull(),
                    () -> assertThatIllegalStateException().as("another invalid bean").isThrownBy(new AnotherInvalidBeanBuilder()::build)
            );
        }

        @DisplayName("should suppress build validation only a given number of times")
        @Test void shouldSuppressBuildValidationGivenNumberOfTimes() {
            ValidationResultExtension.suppressValidationFor(InvalidBean.class, 2);

            assertAll(
                    () -> assertThat(new InvalidBeanBuilder().build()).isNotNull(),
                    () -> assertThat(new InvalidBeanBuilder().build()).isNotNull(),
                    () -> assertThrows(IllegalStateException.class, new InvalidBeanBuilder()::build)
            );
        }
    }

    private class InvalidBean {
    }

    private class AnotherInvalidBean {
    }

    class InvalidBeanBuilder extends AbstractBuilder<InvalidBean> {
        InvalidBeanBuilder() {
            super(validInstance -> ValidationResult.validate(InvalidBean.class).notFalse("aField", () -> false, "validation cannot be false").returnResult());
        }

        @Override
        protected InvalidBean buildBean() {
            return new InvalidBean();
        }
    }

    class AnotherInvalidBeanBuilder extends AbstractBuilder<AnotherInvalidBean> {
        AnotherInvalidBeanBuilder() {
            super(validInstance -> ValidationResult.validate(AnotherInvalidBean.class).notFalse("aField", () -> false, "validation cannot be falseddddddddsdfadfasdfasdf").returnResult());
        }

        @Override
        protected AnotherInvalidBean buildBean() {
            return new AnotherInvalidBean();
        }
    }
