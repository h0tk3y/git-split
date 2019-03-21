# git-split
A small command-line tool that copies files in a Git repository preserving the history for all files

> Note: the resulting commits will produce a conflict when rebased. To avoid this, use the `--rebase-merges` option of `git-rebase`.

Get a distribution from the [releases page](https://github.com/h0tk3y/git-split/releases).

![resulting tree](https://user-images.githubusercontent.com/1888526/53649646-aa61d080-3c53-11e9-85a6-83336ac7e1e4.png)

```
Usage:
git-split {--from filename --to {filename}+ }+
git-split --instruction filename

The --instruction file format is the same as command line format, with arguments
put on separate lines, for example:

--from
  abc.txt
--to
  abcCopy1.txt
  abcCopy2.txt

--from
  def.txt
--to
  defCopy.txt
```

Example:

```
git-split --from abc.txt --to abcCopy1.txt abcCopy2.txt
```

```
git-split --instruction instruction.txt
```
