import { StringArgument } from "../../src/argument";

test("testParse", () => {
    const argument = new StringArgument("--argument", [], "description", false, "");

    argument.parse("test");

    expect(argument.value).toBe("test");
});
