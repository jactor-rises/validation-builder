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

    @Test
    void shouldNotInitNamedEraWithoutName() {
        IllegalStateException e = assertThrows(
                IllegalStateException.class, () -> new NamedEraBuilder().build()
        );

        assertThat(e.getMessage(), equalTo("A named era must have a name"));
    }

    @Test
    void shouldNotInitNamedAreatWithoutBeginning() {
        IllegalStateException e = assertThrows(
                IllegalStateException.class, () -> new NamedEraBuilder().withName("An era").build()
        );

        assertThat(e.getMessage(), equalTo("An era must have a beginning"));
    }

    @Test
    void shouldNotInitNamedAreatWithBeginningAfterTheEnd() {
        IllegalStateException e = assertThrows(
                IllegalStateException.class, () -> new NamedEraBuilder()
                        .withName("An era")
                        .withBeginning(LocalDate.now())
                        .withEnd(LocalDate.now().minusDays(1))
                        .build()
        );

        assertThat(e.getMessage(), equalTo("An era cannot end before it is started"));
    }

    @Test
    void shouldInitValidInstanceOnlyApplyingNameAndBeginning() {
        assertNotNull(new NamedEraBuilder().withName("An era").withBeginning(LocalDate.now()).build());
    }


## Test of `NamedEra` (sample of a bean using a builder)

    @Test
    void shouldCalculateNoOfMonthsInAnEra() {
        JUnitValidationBuilder.suppressOneValidationFor(NamedEra.class);
        NamedEra namedEra = NamedEra.init()
                .withBeginning(LocalDate.now().minusYears(3))
                .withEnd(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS), is(equalTo(24L)));
    }

    @Test
    void shouldCalculateEraLenghtUsingToDaysDateWhenNoEndDateIsSpecified() {
        JUnitValidationBuilder.suppressOneValidationFor(NamedEra.class);
        NamedEra namedEra = NamedEra.init()
                .withBeginning(LocalDate.now().minusYears(1))
                .build();

        assertThat(namedEra.calculateLength(ChronoUnit.MONTHS), is(equalTo(12L)));
    }


### Test of `ValidationBuilder`

    @Test
    void shouldReturnValidatedBean() {
        validationBuilder = new TestValidationBuilder(bean -> Optional.empty());
        Bean bean = validationBuilder.build();

        assertThat(bean, is(notNullValue()));
    }

    @Test
    void shouldFailValidationOfBean() {
        validationBuilder = new TestValidationBuilder(bean -> Optional.of("invalid"));

        IllegalStateException iae = assertThrows(IllegalStateException.class, () -> validationBuilder.build());

        assertThat(iae.getMessage(), equalTo("invalid"));
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
