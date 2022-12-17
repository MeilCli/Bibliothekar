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

export interface PomParent {
    group: string;
    artifact: string;
    version: string;
}

export interface PomOrganization {
    name: string | null;
}

export interface PomLicense {
    name: string | null;
    url: string | null;
}

export interface PomDeveloper {
    name: string | null;
    organization: string | null;
}
