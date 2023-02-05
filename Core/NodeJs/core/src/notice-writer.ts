import * as fs from "fs";
import * as path from "path";
import { Notice } from "./notice";

export class NoticeWriter {
    constructor(private directory: string) {}

    willOverrides(notices: Notice[]): Notice[] {
        return notices.filter((x) => fs.existsSync(this.path(x)));
    }

    willWrites(notices: Notice[]): string[] {
        return notices.map((x) => this.path(x));
    }

    write(notices: Notice[]) {
        if (fs.existsSync(this.directory) == false) {
            fs.mkdirSync(this.directory);
        }

        for (const notice of notices) {
            fs.writeFileSync(this.path(notice), JSON.stringify(notice, undefined, 2));
        }
    }

    private path(notice: Notice): string {
        return path.join(this.directory, encodeURIComponent(notice.packageId));
    }
}
