import { Argument, BooleanArgument } from "./argument";
import { Command } from "./command";
import { Context } from "./context";
import { PrettyLogger } from "./pretty-logger";

export class HelpCommand extends Command {
    name = "help";
    description = "show help";
    subCommands: Command[] = [];
    argumentList: Argument<unknown>[] = [];

    constructor(private target: Command | Command[]) {
        super();
    }

    execute(context: Context) {
        if (this.target instanceof Command) {
            this.showSingleCommand(this.target, context);
        } else {
            this.showMultipleCommand(this.target);
        }
    }

    private showSingleCommand(command: Command, context: Context) {
        const commandText = context.commandCallings.slice(0, context.commandCallings.length - 1).join(" ");
        let postfix = "";
        if (command.subCommands.length != 0 && command.argumentList.length != 0) {
            postfix = " [commands/options]";
        } else if (command.subCommands.length != 0) {
            postfix = " [commands]";
        } else if (command.argumentList.length != 0) {
            postfix = " [options]";
        }
        console.log(`Usage: ${commandText} ${postfix}`);
        console.log(command.description);

        if (command.subCommands.length != 0) {
            console.log();
            console.log("[commands]");
            const pretttyLogger = new PrettyLogger();
            for (const subCommand of command.subCommands) {
                pretttyLogger.add(`  ${subCommand.name}`, subCommand.description);
            }
            pretttyLogger.execute();
        }

        if (command.argumentList.length != 0) {
            console.log();
            console.log("[options]");
            const prettyLogger = new PrettyLogger();
            for (const argument of command.argumentList) {
                const argumentKeywords = [argument.name, ...argument.aliases];
                if (argument instanceof BooleanArgument) {
                    prettyLogger.add(`  ${argumentKeywords.join(" ")}`, argument.description);
                } else {
                    prettyLogger.add(`  ${argumentKeywords.join(" ")}  <value>`, argument.description);
                }
            }
            prettyLogger.execute();
        }
    }

    private showMultipleCommand(commands: Command[]) {
        console.log("[commands]");
        const pretttyLogger = new PrettyLogger();
        for (const command of commands) {
            pretttyLogger.add(`  ${command.name}`, command.description);
        }
        pretttyLogger.execute();
    }
}
