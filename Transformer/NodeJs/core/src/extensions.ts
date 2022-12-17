export {};

declare global {
    interface Array<T> {
        let(delegate: (source: Array<T>) => Array<T>): Array<T>;
    }
}

Array.prototype.let = function <T>(delegate: (source: Array<T>) => Array<T>): Array<T> {
    return delegate(this);
};
