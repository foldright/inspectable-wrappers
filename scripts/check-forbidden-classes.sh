#!/bin/bash
set -eEuo pipefail

# cd to project root dir
cd "${0%/*}"/..

readonly forbidden_classes=(
  # prefer edu.umd.cs.findbugs.annotations.Nullable
  javax.annotation.Nullable
  org.jetbrains.annotations.Nullable

  # prefer edu.umd.cs.findbugs.annotations.NonNull
  javax.annotation.Nonnull
  org.jetbrains.annotations.NotNull

  # prefer edu.umd.cs.findbugs.annotations.CheckForNull
  javax.annotation.CheckReturnValue
  org.jetbrains.annotations.CheckReturnValue

  # prefer @edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters(NonNull.class)
  javax.annotation.ParametersAreNonnullByDefault

  # prefer static import methods of `Assertions`
  org.junit.jupiter.api.Assertions\;
)

grep_pattern=$(printf '%s\n' "${forbidden_classes[@]}")
[[ "${GITHUB_ACTIONS:-}" = true || -t 1 ]] && more_grep_options=(--color=always)
readonly grep_pattern more_grep_options

! grep "$grep_pattern" -F ${more_grep_options[@]:+"${more_grep_options[@]}"} -n -C2 -r src/
