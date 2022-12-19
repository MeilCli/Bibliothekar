# NodeJs Development
Bibliothekar is using TypeScript for NodeJs development.  

## Setup
1. Install [VS Code](https://code.visualstudio.com/)
1. Fork this repository, and clone forked-repository
1. Open forked-repository on VS Code
1. Install VS Code extensions
   - Maybe, VS Code will recommend for you
   - Install-required: 
     - [Prettier - Code formatter](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)
     - [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint)
1. Install Node.js
   - install version is writen at .nvmrc
   - if you use nvm, you can only execute `nvm use`
   - if you use [nvm-windows](https://github.com/coreybutler/nvm-windows), you can only execute `nvm use $(Get-Content .nvmrc)`

## Coding convention
### Naming
- General:
  - Don't abbreviate word
    - Exception-rule is only `Id` and `Ok`
  - Acronym word can use, but don't use word that don't found Google-search
    - Regardless of the number of letters, it is treated as one word.
    - for example: `HtmlLogger`, `XmlLogger`

## Other
- if auto-guard marked file changed, run `npm run generate:guard`