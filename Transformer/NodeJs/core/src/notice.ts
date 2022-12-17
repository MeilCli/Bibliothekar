export interface Notice {
    packageId: string;
    groupId: string | null;
    name: string | null;
    description: string | null;
    version: string | null;
    url: string | null;
    repositoryUrl: string | null;
    author: string | null;
    licenses: NoticeLicense[];
}

export interface NoticeLicense {
    name: string | null;
    url: string | null;
    text: string | null;
}
