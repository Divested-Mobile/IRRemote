# Contributing To IR Remote

Thank you for your interest in contributing to IR Remote! We welcome everyone who wants to help us improve this project. This document explains how you can contribute to the project in a constructive way that helps us get your changes merged quickly.

## Table Of Contents

* [How To Contribute](#how-to-contribute)
* [How To Add IR Codes](#how-to-add-ir-codes)
* [Git Commit Messages](#git-commit-messages)

## How To Contribute

Before you get started, please read the [README](README.md) file for an overview of the project. If you want to contribute code, please follow these steps:

* Fork the repository and create a new branch based on the latest `libre` branch.
* Make your changes in the new branch. Write clear commit messages that describe the changes you made.
* When you are ready to submit your changes, push your branch to your fork and create a merge request (MR) to the `libre` branch of the original repository.
* Wait for a maintainer to review your changes. We shall provide feedback and ask you to make any necessary changes.
* Once your changes are approved, we shall merge your MR into the `libre` branch.

If you are not sure what to work on, check out the [Issues] page. You might find a bug to fix or a new feature to implement. Please comment on issues to let us know you are working on them so we can avoid duplication of effort.

You can also contribute by reporting bugs and suggesting new features. Use the [Issues] page to open new issues. Please search the existing issues before submitting a new one to avoid duplicates. Write clear descriptions of the issue you found and steps to reproduce it. If you are suggesting a new feature, describe how it will work and why you think it is useful.

[Issues]: https://gitlab.com/divested-mobile/irremote/-/issues

## How To Add IR Codes

If you want to contribute IR codes for new devices or improve existing codes, please follow these steps:

* Check the [existing IR codes](app/src/main/assets/db) directory to make sure the device you want to add codes for is not already included. If it is, consider improving the existing codes instead of adding duplicates.
* If the device vendor name does not exist, create a new folder with the vendor name in the relevant directory under [db](app/src/main/assets/db).
* This project uses the pronto hex code format. More information about the pronto hex format can be found in [RemoteCentral](https://www.remotecentral.com/features/irdisp2.htm). You can find those code using a IR remote control app or device, or by using a IR receiver and an Arduino or Raspberry Pi.
* Use either button files or `remote.json`:
   * Create a separate `.button` file for each button and save them all in a relevant directory for that remote. The button file should be named as `b_<id>.button`. The `id` number should match with the IDs in [Button.java](app/src/main/java/org/twinone/irremote/components/Button.java) file.
   * Create `remote.json` file. Check existing ones for file format examples or just export one of your remotes. 
* Test the new IR codes with a compatible device and verify their functionality.
* Commit and push your changes to your forked repository and follow the steps in above section.

## Git Commit Messages

When contributing to the project, please make sure to write clear and concise commit messages that accurately describe the changes being made. Good commit messages help other developers understand the purpose, scope, and impact of your changes. To write effective commit messages, follow this format:

```
<type>: <short summary>

<long description>
```

- **Type**: Use one of the following keywords to identify the type of changes you are making:
  - `build`: changes to any files related to gradle project, configuration or dependencies.
  - `ci`: changes to CI files.
  - `db`: adding or changing any remote button files.
  - `docs`: documentation files updated.
  - `java`: changes to actual application code.
  - `res`: adding or changing resource files - icons, styles, themes etc.
- **Short summary**: A one-line summary of the changes made, written in the present tense and imperative mood.
- **Long description**: Elaborate on the changes made, explaining why they were necessary or how they address a specific issue. If the merge request fixes any issue, add the link here.

* When only changing documentation, include `[ci skip]` at first in the commit title.
