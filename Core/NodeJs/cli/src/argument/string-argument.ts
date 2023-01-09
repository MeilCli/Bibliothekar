import { Argument } from "./argument";

export class StringArgument implements Argument<string> {
    public value: string;

    constructor(
        public readonly name: string,
        public readonly aliases: string[],
        public readonly description: string,
        public readonly required: boolean,
        defaultValue: string
    ) {
        this.value = defaultValue;
    }

    parse(value: string | undefined) {
        if (value == undefined) {
            throw Error("value is not provided");
        }
        this.value = value;
    }
}
