#!/bin/bash
# usage:From the sp-flex-yellow-android  repo root directory, run scripts/generateProtos.sh [BUF_TOKEN]
# or if you are in cells directory then scripts/generateProtos.sh [BUF_TOKEN]
#
rm -r wire-proto/src/commonMain/kotlin
./gradlew :wire-compiler:run \
  --quiet \
  --args='--kotlin_out=../wire-proto/src/commonMain/kotlin --proto_path=../grpc-server/src/main/proto --kotlin_rpc_call_style=suspending'
if [ $? -ne 0 ]; then
  echo "Failed to generate Wire protos"
  exit $?
fi
exit 0