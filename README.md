# git-split
A small command-line tool that copies files in a Git repository preserving the history for all files

Get a distribution from the [releases page](https://github.com/h0tk3y/git-split/releases).

![resulting tree](https://user-images.githubusercontent.com/1888526/53649646-aa61d080-3c53-11e9-85a6-83336ac7e1e4.png)

```
Usage:
git-split {--from filename {--to filename}+ }+
git-split --instruction filename

The --instruction file format is the same as command line format, with arguments
put on separate lines, for example:

--from
  abc.txt
--to
  abc1.txt
  abc2.txt

--from
  def.txt
--to
  def123.txt
```
