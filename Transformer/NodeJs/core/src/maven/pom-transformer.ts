import "../extensions";
import { Notice, NoticeLicense } from "bibliothekar-core-core";
import { Transformer } from "../transformer";
import { Pom } from "./pom";

export class PomTransformer implements Transformer<Pom> {
    transform(value: Pom): Notice {
        const packageId = `${value.group}:${value.artifact}`;
        const groupId = value.group;
        const name = value.name;
        const description = value.description;
        const version = value.version;
        const url = value.url;
        const repositoryUrl = null;
        const author =
            value.organization?.name ??
            value.developers
                ?.map((x) => x.organization ?? x.name)
                ?.let((x) => [...new Set(x)])
                ?.join(", ") ??
            null;
        const licenses: NoticeLicense[] = [];
        for (const pomLicense of value.licenses ?? []) {
            licenses.push({
                name: pomLicense.name,
                url: pomLicense.url,
                text: null,
            });
        }
        return {
            packageId,
            groupId,
            packageManagement: "maven",
            name,
            description,
            version,
            url,
            repositoryUrl,
            author,
            licenses,
        };
    }
}
