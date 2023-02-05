export class PrettyLogger {
    private lines: [string, string, string][] = [];

    add(left: string, right: string, uncountTexts: string[] = []) {
        let countableLeft = left;
        for (const uncountText of uncountTexts) {
            countableLeft = countableLeft.replaceAll(uncountText, "");
        }
        this.lines.push([countableLeft, left, right]);
    }

    execute() {
        const maxLength = this.lines.map((x) => x[0].length).reduce((x, y) => (x < y ? y : x));

        for (const line of this.lines) {
            console.log(`${line[1].padEnd(maxLength + (line[1].length - line[0].length), " ")}  ${line[2]}`);
        }
    }
}
