<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script setup lang="ts">
const url = ref('https://github.com/nottyjay/d3code-boot-plus')

function goto() {
  window.open(url.value)
}
</script>
