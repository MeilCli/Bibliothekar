import { BooleanArgument } from "../../src/argument";

test("testParse_undefined", () => {
    const argument = new BooleanArgument("--argument", [], "description", false, false);

    argument.parse(undefined);

    expect(argument.value).toBe(true);
});

test("testParse_true", () => {
    const argument = new BooleanArgument("--argument", [], "description", false, false);

    argument.parse("true");

    expect(argument.value).toBe(true);
});

test("testParse_t", () => {
    const argument = new BooleanArgument("--argument", [], "description", false, false);

    argument.parse("t");

    expect(argument.value).toBe(true);
});

test("testParse_false", () => {
    const argument = new BooleanArgument("--argument", [], "description", false, true);

    argument.parse("false");

    expect(argument.value).toBe(false);
});

test("testParse_f", () => {
    const argument = new BooleanArgument("--argument", [], "description", false, true);

    argument.parse("f");

    expect(argument.value).toBe(false);
});
