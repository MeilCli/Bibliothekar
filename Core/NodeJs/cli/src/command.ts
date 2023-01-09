import { Argument } from "./argument";
import { Context } from "./context";

export abstract class Command {
    abstract name: string;
    abstract description: string;
    abstract subCommands: Command[];
    abstract arguments: Argument<unknown>[];

    abstract execute(context: Context): void;
}
