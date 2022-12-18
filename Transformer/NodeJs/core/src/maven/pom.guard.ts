/*
 * Generated type guards for "pom.ts".
 * WARNING: Do not manually change this file.
 */
import { Pom, PomParent, PomOrganization, PomLicense, PomDeveloper } from "./pom";

export function isPom(obj: unknown): obj is Pom {
    const typedObj = obj as Pom;
    return (
        ((typedObj !== null && typeof typedObj === "object") || typeof typedObj === "function") &&
        typeof typedObj["group"] === "string" &&
        typeof typedObj["artifact"] === "string" &&
        typeof typedObj["version"] === "string" &&
        (typedObj["name"] === null || typeof typedObj["name"] === "string") &&
        (typedObj["description"] === null || typeof typedObj["description"] === "string") &&
        (typedObj["url"] === null || typeof typedObj["url"] === "string") &&
        (typedObj["parent"] === null || (isPomParent(typedObj["parent"]) as boolean)) &&
        (typedObj["organization"] === null || (isPomOrganization(typedObj["organization"]) as boolean)) &&
        (typedObj["licenses"] === null ||
            (Array.isArray(typedObj["licenses"]) &&
                typedObj["licenses"].every((e: any) => isPomLicense(e) as boolean))) &&
        (typedObj["developers"] === null ||
            (Array.isArray(typedObj["developers"]) &&
                typedObj["developers"].every((e: any) => isPomDeveloper(e) as boolean)))
    );
}

export function isPomParent(obj: unknown): obj is PomParent {
    const typedObj = obj as PomParent;
    return (
        ((typedObj !== null && typeof typedObj === "object") || typeof typedObj === "function") &&
        typeof typedObj["group"] === "string" &&
        typeof typedObj["artifact"] === "string" &&
        typeof typedObj["version"] === "string"
    );
}

export function isPomOrganization(obj: unknown): obj is PomOrganization {
    const typedObj = obj as PomOrganization;
    return (
        ((typedObj !== null && typeof typedObj === "object") || typeof typedObj === "function") &&
        (typedObj["name"] === null || typeof typedObj["name"] === "string")
    );
}

export function isPomLicense(obj: unknown): obj is PomLicense {
    const typedObj = obj as PomLicense;
    return (
        ((typedObj !== null && typeof typedObj === "object") || typeof typedObj === "function") &&
        (typedObj["name"] === null || typeof typedObj["name"] === "string") &&
        (typedObj["url"] === null || typeof typedObj["url"] === "string")
    );
}

export function isPomDeveloper(obj: unknown): obj is PomDeveloper {
    const typedObj = obj as PomDeveloper;
    return (
        ((typedObj !== null && typeof typedObj === "object") || typeof typedObj === "function") &&
        (typedObj["name"] === null || typeof typedObj["name"] === "string") &&
        (typedObj["organization"] === null || typeof typedObj["organization"] === "string")
    );
}
