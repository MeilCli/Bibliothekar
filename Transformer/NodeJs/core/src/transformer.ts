import { Notice } from "bibliothekar-core-core";

export interface Transformer<T> {
    transform(value: T): Notice;
}
