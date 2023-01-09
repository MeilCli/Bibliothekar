import * as fs from "fs";
import * as path from "path";
import { Reader } from "../reader";
import { Pom } from "./pom";
import { isPom } from "./pom.guard";

const pomFileName = "pom.json";

export class PomReader implements Reader<Pom> {
    read(directory: string): Pom[] {
        if (fs.existsSync(directory) == false) {
            throw Error(`${directory} cannot found`);
        }
        const result: Pom[] = [];

        const pomFiles = this.readPath(directory).filter((x) => path.basename(x) == pomFileName);
        for (const pomFile of pomFiles) {
            const json = JSON.parse(fs.readFileSync(pomFile).toString());
            if (isPom(json)) {
                result.push(json);
            } else {
                console.warn(`found invalid value: (${pomFile})`);
                console.warn(json);
            }
        }

        return result;
    }

    private readPath(target: string): string[] {
        const stat = fs.statSync(target);
        if (stat.isFile()) {
            return [target];
        }
        if (stat.isDirectory()) {
            return fs.readdirSync(target).flatMap((x) => this.readPath(path.join(target, x)));
        }

        return [];
    }
}
