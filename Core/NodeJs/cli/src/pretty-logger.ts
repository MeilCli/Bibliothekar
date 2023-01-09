export class PrettyLogger {
    private lines: [string, string][] = [];

    add(left: string, right: string) {
        this.lines.push([left, right]);
    }

    execute() {
        const maxLength = this.lines.map((x) => x[0].length).reduce((x, y) => (x < y ? y : x));

        for (const line of this.lines) {
            console.log(`${line[0].padEnd(maxLength, " ")}  ${line[1]}`);
        }
    }
}
