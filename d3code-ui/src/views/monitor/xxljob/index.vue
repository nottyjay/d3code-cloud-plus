<template>
  <div>
    <i-frame v-model:src="url" sandbox="allow-same-origin allow-scripts allow-forms"></i-frame>
  </div>
</template>

<script setup lang="ts">
const url = ref(import.meta.env.VITE_APP_XXLJOB_ADMIN)
</script>
