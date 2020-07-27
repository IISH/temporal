#!/bin/bash
#
# backup_ftp/startup.sh

FILESET="$1"

# tar and rsync
# Copy the fileset from the local location to the remote location
# Then move it to the sorting area



# BACKUP_FTP_HOST : the host of the backup storage
export BACKUP_FTP_HOST="archive.surfsara.nl"

# BACKUP_FTP_USER : account username BACKUP_FTP_HOST
export BACKUP_FTP_USER="huc-iisg"


function main() {

  # ARCHIVAL_ID : the accession number of the filesset.
  export PARENT_DIR=$(dirname "$FILESET")
  export ARCHIVAL_ID=$(basename "$FILESET")
  archive="${PARENT_DIR}/${ARCHIVAL_ID}.7z"

  echo "Create package ${archive}"
  /usr/bin/7z a "$archive" "$FILESET"

  echo "rsync the package to ${BACKUP_FTP_HOST}"
  /usr/bin/rsync -av --progress "$archive" "$BACKUP_FTP_USER"@"${BACKUP_FTP_HOST}:workflow_backup/"

  rm "$archive"
}


main

exit 0
