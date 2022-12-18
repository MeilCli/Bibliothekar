/**
 * @see {isPom} ts-auto-guard:type-guard
 */
export interface Pom {
    group: string;
    artifact: string;
    version: string;
    name: string | null;
    description: string | null;
    url: string | null;
    parent: PomParent | null;
    organization: PomOrganization | null;
    licenses: PomLicense[] | null;
    developers: PomDeveloper[] | null;
}

/**
 * @see {isPomParent} ts-auto-guard:type-guard
 */
export interface PomParent {
    group: string;
    artifact: string;
    version: string;
}

/**
 * @see {isPomOrganization} ts-auto-guard:type-guard
 */
export interface PomOrganization {
    name: string | null;
}

/**
 * @see {isPomLicense} ts-auto-guard:type-guard
 */
export interface PomLicense {
    name: string | null;
    url: string | null;
}

/**
 * @see {isPomDeveloper} ts-auto-guard:type-guard
 */
export interface PomDeveloper {
    name: string | null;
    organization: string | null;
}
