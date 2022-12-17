export interface Reader<T> {
    read(directory: string): T[];
}
