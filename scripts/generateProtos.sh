#!/bin/bash
# usage:From the sp-flex-yellow-android  repo root directory, run scripts/generateProtos.sh [BUF_TOKEN]
# or if you are in cells directory then scripts/generateProtos.sh [BUF_TOKEN]
#
if [[ "$OSTYPE" == "darwin"* ]]; then
  # macOS host
  if ! command -v gsed &> /dev/null; then
    echo "Installing gnu-sed via Homebrew..."
    brew install gnu-sed
  fi
  echo "Linking gsed to ./gsed..."
  ln -s $(which gsed) ${current_dir}/gsed
else
  # Linux host
  if ! command -v gsed &> /dev/null; then
    echo "Linking gsed to /usr/bin/sed..."
    ln -s $(which sed) ${current_dir}/gsed
  fi
fi
export PATH=${current_dir}:$PATH
rm -r mcp-proto/src/commonMain/kotlin
./gradlew :wire-compiler:run \
  --quiet \
  --args='--kotlin_out=../mcp-proto/src/main/kotlin --proto_path=../grpc-server/src/main/proto --kotlin_rpc_call_style=suspending'
if [ $? -ne 0 ]; then
  echo "Failed to generate Wire protos"
  exit $?
fi
echo "Removing public modifier from generated files and replacing vararg with val..."
find ./mcp-proto/src/main/kotlin -name "*.kt" -exec gsed -i 's/public //g' {} +
find ./mcp-proto/src/main/kotlin -name "*.kt" -exec gsed -i 's/vararg /val /g' {} +
export EXIT_CODE=$?
echo "Unlinking gsed from ./gsed..."
unlink gsed
exit $EXIT_CODE
exit 0