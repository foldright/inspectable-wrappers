#!/bin/bash
set -eEuo pipefail

# cd to project root dir
cd "${0%/*}"/..

readonly forbidden_classes=(
  # use edu.umd.cs.findbugs.annotations.Nullable
  javax.annotation.Nullable
  org.jetbrains.annotations.Nullable

  # use edu.umd.cs.findbugs.annotations.NonNull
  javax.annotation.Nonnull
  org.jetbrains.annotations.NotNull

  # use edu.umd.cs.findbugs.annotations.CheckForNull
  javax.annotation.CheckReturnValue
  org.jetbrains.annotations.CheckReturnValue

  # use @edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters(NonNull.class)
  javax.annotation.ParametersAreNonnullByDefault
)

grep_pattern=$(printf '%s\n' "${forbidden_classes[@]}")

grep_options=("$grep_pattern" -F -n -C2 -r src/)
[[ "${GITHUB_ACTIONS:-}" = true || -t 1 ]] && grep_options=("${grep_options[@]}" --color=always)

! grep "${grep_options[@]}"
