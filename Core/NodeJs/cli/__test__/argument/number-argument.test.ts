import { NumberArgument } from "../../src/argument";

test("testParse", () => {
    const argument = new NumberArgument("--argument", [], "description", false, 0);

    argument.parse("1");

    expect(argument.value).toBe(1);
});
