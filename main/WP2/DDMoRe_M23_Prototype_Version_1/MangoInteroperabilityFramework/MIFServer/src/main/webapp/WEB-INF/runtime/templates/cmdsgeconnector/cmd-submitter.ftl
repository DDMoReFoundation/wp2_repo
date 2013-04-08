#!${GRID_SHELL}

SGE_INITIALISATION_SCRIPT_PATH=${SGE_INITIALISATION_SCRIPT_PATH!}; export SGE_INITIALISATION_SCRIPT_PATH
# REQUEST_ATTRIBUTE_BLOCK
${REQUEST_ATTRIBUTE_BLOCK}
# GRID_HOST_PREAMBLE
${GRID_HOST_PREAMBLE}
 
MIF_TIMESTAMP_FILE="${job.getGridJobDirectoryMifHiddenDir()}/.TIMESTAMP"
MIF_LIST_FILE="${job.getGridJobDirectoryMifHiddenDir()}/MIF.list"

if ! (touch $MIF_TIMESTAMP_FILE); then
    echo "$0: failed to create $MIF_TIMESTAMP_FILE file" >&2
    exit 3
fi

# We need to sleep for 1 second here because some versions of find can't cope with
# files less than a second newer.
sleep 1

${job.executionRequest.executionParameters}
ext=$?
cd "${job.gridJobDirectory}"
if ! find . -newer $MIF_TIMESTAMP_FILE -type f -print > $MIF_LIST_FILE; then
    exit $?
fi
exit $ext