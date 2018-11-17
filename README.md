# Validation Builder

## Purpose

A simple builder to allow a bean instance to have a valid state without constructors containing validation logic. This is
done using a builder which initializes each bean in a valid state using a lambda expression to evaluate the state of the
instance. 

This will simplyfy reading of code and will prevent compile time changes when adding or removing data needed in order to
evaluate the bean state. A valid instance that is supposed to be interacted with without using mutability-disadvantgeges
of setters. In order to change the state of the bean, it should be done in "intelligent" methods.

This will also simplify unit testing as it is possible to turn of validation during testing allowing to only test with
the data needed for the test.

## Advantages of use

* simpler code to read
* immutable beans or beans only mutable through methods (no setters need to be puplic and no public constructor)
* test validation of valid instances
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
        builder = new AbstractBuilder<Bean>(b -> Optional.of("invalid")) {
            @Override protected Bean buildBean() {
                return new Bean();
            }
        };

        assertThatIllegalStateException().isThrownBy(() -> builder.build())
                .withMessage("invalid");
    }

    private class Bean {
    }


### Test of `JUnitValidationBuilder`

    @Test
    void shouldSuppressBuildValidation() {
        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> new InvalidBeanBuilder().build()),
                () -> {
                    JUnitValidationBuilder.suppressOneValidationFor(InvalidBean.class);
                    assertThat(new InvalidBeanBuilder().build(), is(notNullValue()));
                }
        );
    }

    @Test
    void shouldSuppressBuildValidationOnlyForGivenClass() {
        JUnitValidationBuilder.suppressOneValidationFor(InvalidBean.class);

        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> new AnotherInvalidBeanBuilder().build()),
                () -> assertThat(new InvalidBeanBuilder().build(), is(notNullValue()))
        );
    }

    @Test
    void shouldSuppressBuildValidationGivenNumberOfTimes() {
        JUnitValidationBuilder.suppressValidation(InvalidBean.class, 2);

        assertAll(
                () -> assertThat(new InvalidBeanBuilder().build(), is(notNullValue())),
                () -> assertThat(new InvalidBeanBuilder().build(), is(notNullValue())),
                () -> assertThrows(IllegalStateException.class, () -> new InvalidBeanBuilder().build())
        );
    }
