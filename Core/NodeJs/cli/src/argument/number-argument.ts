import { Argument } from "./argument";

export class NumberArgument implements Argument<number> {
    public value: number;

    constructor(
        public readonly name: string,
        public readonly aliases: string[],
        public readonly description: string,
        public readonly required: boolean,
        defaultValue: number
    ) {
        this.value = defaultValue;
    }

    parse(value: string | undefined) {
        if (value == undefined || Number.isInteger(value) == false) {
            throw Error("value is not number");
        }
        this.value = Number.parseInt(value);
    }
}
