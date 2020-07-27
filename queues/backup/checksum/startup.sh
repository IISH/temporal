#!/bin/bash
#
# checksum/startup.sh

FILESET="$1"

# main
# If we find a file that may contain checksums, use it for checking.
# The guess is that the file has the algoritm in it:
# [filename].[md5 or any other of sha1 sha224 sha256 sha384 sha512]
# or
# [md5 or any other of sha1 sha224 sha256 sha384 sha512]sum.txt
#
# If not there, create a md5sum checklist.
function main() {
  cd "$FILESET" || exit 1
  for ext in md5 sha1 sha224 sha256 sha384 sha512
  do
    for filename in *".${ext}"
    do
      file_with_checksums="${filename}"
      if [ -f "$file_with_checksums" ]
      then
        echo "Check sum with ${ext}"
        eval "${ext}sum -c ${file_with_checksums}"
      fi
    done
    file_with_checksums="${ext}sum.txt"
    if [ -f "$file_with_checksums" ]
    then
      echo "Check sum with ${ext}"
      eval "${ext}sum -c ${file_with_checksums}"
    fi
  done
}


main

exit 0
