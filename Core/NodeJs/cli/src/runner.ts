import { Command } from "./command";
import { Context } from "./context";
import { HelpCommand } from "./help-command";

export class Runner {
    private context: Context = new Context();

    constructor(private commands: Command[]) {}

    run() {
        this.runWithArguments(process.argv.slice(2));
    }

    runWithArguments(argumentList: string[]) {
        if (argumentList.length == 0) {
            this.execute(new HelpCommand(this.commands), []);
            return;
        }
        let command = this.commands.find((x) => x.name == argumentList[0]);
        if (command == undefined) {
            command = new HelpCommand(this.commands);
        }
        this.execute(command, argumentList.slice(1));
    }

    private execute(command: Command, argumentList: string[]) {
        this.context.commandCallings.push(command.name);

        if (1 <= argumentList.length) {
            const subCommand = command.subCommands.find((x) => x.name == argumentList[0]);
            if (subCommand != undefined) {
                this.execute(subCommand, argumentList.slice(1));
                return;
            }
        }

        let required = command.argumentList.filter((x) => x.required);

        for (let i = 0; i < argumentList.length; i++) {
            const current = argumentList[i];
            let next = i + 1 < argumentList.length ? argumentList[i + 1] : undefined;
            if (next?.startsWith("-")) {
                next = undefined;
            }
            const argument = command.argumentList.find((x) => x.name == current || x.aliases.includes(current));
            if (argument != undefined) {
                argument.parse(next);
                required = required.filter((x) => x.name != current && x.aliases.includes(current) == false);
                if (next != undefined) {
                    i += 1;
                }
            }
        }

        if (0 < required.length) {
            console.error(`required arguments(${required.map((x) => x.name).join()})`);
            return;
        }

        try {
            command.execute(this.context);
        } catch (error) {
            console.error(error);
        }
    }
}
