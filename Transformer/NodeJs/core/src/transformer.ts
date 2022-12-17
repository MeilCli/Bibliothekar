import { Notice } from "./notice";

export interface Transformer<T> {
    transform(value: T): Notice;
}
