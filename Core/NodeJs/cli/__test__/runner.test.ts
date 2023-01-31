import { Runner, Command, Context } from "../src";
import { Argument, StringArgument } from "../src/argument";

interface TestCommandResult {
    commandName: string;
    optionalArgument: string;
    requiredArgument: string;
}

class TestCommand extends Command {
    optionalArgument = new StringArgument("--optional", ["-o"], "optional argument", false, "default of optional");
    requiredArgument = new StringArgument("--required", ["-r"], "required argument", true, "default of required");

    override argumentList: Argument<unknown>[] = [this.optionalArgument, this.requiredArgument];

    constructor(
        override name: string,
        override description: string,
        override subCommands: Command[],
        private resultHolder: (testCommandResult: TestCommandResult) => void
    ) {
        super();
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    execute(context: Context) {
        this.resultHolder({
            commandName: this.name,
            optionalArgument: this.optionalArgument.value,
            requiredArgument: this.requiredArgument.value,
        });
    }
}

function createRunner(resultHolder: (testCommandResult: TestCommandResult) => void): Runner {
    const subCommand = new TestCommand("sub", "sub command", [], resultHolder);
    const parentCommand = new TestCommand("parent", "parent command", [subCommand], resultHolder);
    return new Runner([parentCommand]);
}

test("testParentCommand_fullArgumentName_fullArgument", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    runner.runWithArguments(["parent", "--optional", "hello", "--required", "world"]);

    expect(results).toStrictEqual([
        {
            commandName: "parent",
            optionalArgument: "hello",
            requiredArgument: "world",
        } as TestCommandResult,
    ]);
});

test("testParentCommand_fullArgumentName_partialArgument", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    runner.runWithArguments(["parent", "--required", "world"]);

    expect(results).toStrictEqual([
        {
            commandName: "parent",
            optionalArgument: "default of optional",
            requiredArgument: "world",
        } as TestCommandResult,
    ]);
});

test("testParentCommand_shortArgumentName_fullArgument", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    runner.runWithArguments(["parent", "-o", "hello", "-r", "world"]);

    expect(results).toStrictEqual([
        {
            commandName: "parent",
            optionalArgument: "hello",
            requiredArgument: "world",
        } as TestCommandResult,
    ]);
});

test("testParentCommand_shortArgumentName_partialArgument", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    runner.runWithArguments(["parent", "-r", "world"]);

    expect(results).toStrictEqual([
        {
            commandName: "parent",
            optionalArgument: "default of optional",
            requiredArgument: "world",
        } as TestCommandResult,
    ]);
});

test("testParentCommand_failByRequired", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    expect(() => {
        runner.runWithArguments(["parent"]);
    }).toThrow();
});

test("testSubCommand_fullArgumentName_fullArgument", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    runner.runWithArguments(["parent", "sub", "--optional", "hello", "--required", "world"]);

    expect(results).toStrictEqual([
        {
            commandName: "sub",
            optionalArgument: "hello",
            requiredArgument: "world",
        } as TestCommandResult,
    ]);
});

test("testSubCommand_fullArgumentName_partialArgument", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    runner.runWithArguments(["parent", "sub", "--required", "world"]);

    expect(results).toStrictEqual([
        {
            commandName: "sub",
            optionalArgument: "default of optional",
            requiredArgument: "world",
        } as TestCommandResult,
    ]);
});

test("testSubCommand_shortArgumentName_fullArgument", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    runner.runWithArguments(["parent", "sub", "-o", "hello", "-r", "world"]);

    expect(results).toStrictEqual([
        {
            commandName: "sub",
            optionalArgument: "hello",
            requiredArgument: "world",
        } as TestCommandResult,
    ]);
});

test("testSubCommand_shortArgumentName_partialArgument", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    runner.runWithArguments(["parent", "sub", "-r", "world"]);

    expect(results).toStrictEqual([
        {
            commandName: "sub",
            optionalArgument: "default of optional",
            requiredArgument: "world",
        } as TestCommandResult,
    ]);
});

test("testSubCommand_failByRequired", () => {
    const results: TestCommandResult[] = [];
    const resultHolder = (testCommandResult: TestCommandResult) => {
        results.push(testCommandResult);
    };
    const runner = createRunner(resultHolder);

    expect(() => {
        runner.runWithArguments(["parent", "sub"]);
    }).toThrow();
});
