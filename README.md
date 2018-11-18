# Validation Builder

## Purpose

A simple builder to allow a bean instance to have a valid state without constructors containing validation logic. This is
done using a builder which initializes the bean, validates it, then returning it.

This will simplify reading of code and will prevent compile time changes when editing complex constructors. A valid
instance that is supposed to be interacted with without using mutability-disadvantages of setters. In order to change the
state of the bean, it should be done in "intelligent" methods.

This will also simplify unit testing as it is possible to turn of validation during testing allowing to only test with
the data needed for the test.

## Advantages of use

* simpler code to read and maintain
* easier maintenance
* immutable beans or beans only mutable through methods (no setters need to be public and no public constructor)
* simle unit testing of validation for valid instances
* turn of validation in unit testing to provide instances with data only needed for the test

## Usages of the Builder

### Test of `NamedEraBuilder` (sample of a builder implementation)

    @DisplayName("should not build NamedEra without a naming it")
    @Test
    void shouldNotInitNamedEraWithoutName() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder(NamedEra.validate())::build)
                .withMessage("A named era must have a name");
    }

    @DisplayName("should not build NamedEra without a beginning")
    @Test
    void shouldNotInitNamedEraWithoutBeginning() {
        assertThatIllegalStateException().isThrownBy(new NamedEraBuilder(NamedEra.validate()).withName("An era")::build)
                .withMessage("An era must have a beginning");
    }

    @DisplayName("should not build NamedEra when the end date is before the beginning")
    @Test
    void shouldNotInitNamedAreatWithBeginningAfterTheEnd() {
        assertThatIllegalStateException().isThrownBy(() ->
                new NamedEraBuilder(NamedEra.validate())
                        .withName("The era")
                        .withBeginning(LocalDate.now())
                        .withEnd(LocalDate.now().minusDays(1))
                        .build()
        ).withMessage("The era cannot end before it is started");
    }

    @DisplayName("should build a NamedEra when all required ")
    @Test
    void shouldInitValidInstanceOnlyApplyingNameAndBeginning() {
        assertThat(
                new NamedEraBuilder(NamedEra.validate()).withName("An era").withBeginning(LocalDate.now()).build()
        ).isNotNull();
    }


## Test of `NamedEra` (sample of a bean using a builder)

    @DisplayName("should calculate number of months in an era")
    @Test
    void shouldCalculateNoOfMonthsInAnEra() {
        JUnitBuilder.suppressOneValidationFor(NamedEra.class);
        NamedEra namedEra = NamedEra.aNamedEra()
                .withBeginning(LocalDate.now().minusYears(3))
                .withEnd(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(24L);
    }

    @DisplayName("should calculate length of era against todays date when no end date is not specified")
    @Test
    void shouldCalculateEraLenghtUsingToDaysDateWhenNoEndDateIsSpecified() {
        JUnitBuilder.suppressOneValidationFor(NamedEra.class);
        NamedEra namedEra = NamedEra.aNamedEra()
                .withBeginning(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS)).isEqualTo(12L);
    }


### Test of `AbstractBuilder`

    @DisplayName("should build a validated bean")
    @Test
    void shouldReturnValidatedBean() {
        builder = new AbstractBuilder<Bean>(b -> Optional.empty()) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        Bean bean = builder.build();

        assertThat(bean).isNotNull();
    }

    @DisplayName("should fail the build when the instance is not valid")
    @Test
    void shouldFailValidationOfBean() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(new InvalidFields().addWhenNull("fieldName", null))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Invalid field from build: fieldName");
    }

    @DisplayName("should fail the build when the instance is not valid and provide the names of all invalid fields")
    @Test
    void shouldFailValidationOfBeanWithMessageContainingAllTheInvalidFields() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(new InvalidFields()
                        .addWhenNull("fieldName", null)
                        .addWhenNull("anotherField", null)
                )
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Invalid fields from build: fieldName, anotherField");
    }

    @DisplayName("should fail the build when a condition is true")
    @Test
    void shouldFailValidationOfBeanWhenConditionIsTrue() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(new InvalidFields().addWhenTrue("fieldName", () -> true))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Invalid field from build: fieldName");
    }

    @DisplayName("should fail the build when a condition is false")
    @Test
    void shouldFailValidationOfBeanWhenConditionIsFalse() {
        builder = new AbstractBuilder<Bean>(b -> Optional
                .of(new InvalidFields().addWhenFalse("fieldName", () -> false))
        ) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("Invalid field from build: fieldName");
    }

    private class Bean {
    }

###  Test of `JUnitBuilder`

    @DisplayName("should suppress build validation")
    @Test
    void shouldSuppressBuildValidation() {
        assertAll(
                () -> assertThrows(IllegalStateException.class, new InvalidBeanBuilder()::build),
                () -> {
                    JUnitBuilder.suppressOneValidationFor(InvalidBean.class);
                    assertThat(new InvalidBeanBuilder().build()).isNotNull();
                }
        );
    }

    @DisplayName("should suppress build validation for given class only")
    @Test
    void shouldSuppressBuildValidationOnlyForGivenClass() {
        JUnitBuilder.suppressOneValidationFor(InvalidBean.class);

        assertAll(
                () -> assertThrows(IllegalStateException.class, new AnotherInvalidBeanBuilder()::build),
                () -> assertThat(new InvalidBeanBuilder().build()).isNotNull()
        );
    }

    @DisplayName("should suppress build validation only a given number of times")
    @Test
    void shouldSuppressBuildValidationGivenNumberOfTimes() {
        JUnitBuilder.suppressValidation(InvalidBean.class, 2);

        assertAll(
                () -> assertThat(new InvalidBeanBuilder().build()).isNotNull(),
                () -> assertThat(new InvalidBeanBuilder().build()).isNotNull(),
                () -> assertThrows(IllegalStateException.class, new InvalidBeanBuilder()::build)
        );
    }
