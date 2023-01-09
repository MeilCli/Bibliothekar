export interface Argument<T> {
    readonly name: string;
    readonly aliases: string[];
    readonly description: string;
    readonly required: boolean;
    value: T;

    parse(value: string | undefined): void;
}
