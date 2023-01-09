import { Argument } from "./argument";

export class BooleanArgument implements Argument<boolean> {
    public value: boolean;

    constructor(
        public readonly name: string,
        public readonly aliases: string[],
        public readonly description: string,
        public readonly required: boolean,
        defaultValue: boolean
    ) {
        this.value = defaultValue;
    }

    parse(value: string | undefined) {
        if (value == undefined) {
            this.value = true;
        }
        switch (value) {
            case "true":
            case "t":
                this.value = true;
                break;
            case "false":
            case "f":
                this.value = false;
                break;
        }
    }
}
