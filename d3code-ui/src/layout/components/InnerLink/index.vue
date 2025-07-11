<template>
  <div :style="'height:' + height">
    <iframe :id="_iframeId" style="width: 100%; height: 100%; border: 0" :src="_src"></iframe>
  </div>
</template>

<script setup lang="ts">
import { propTypes } from '@/utils/propTypes'

const props = defineProps({
  src: propTypes.string.def('/'),
  iframeId: propTypes.string.isRequired,
})

const _src = ref(props.src)
const _iframeId = ref(props.iframeId)
const height = ref(document.documentElement.clientHeight - 94.5 + 'px')
</script>
