<script setup>
import { ref, watch } from 'vue';
import { StarIcon } from '@heroicons/vue/24/solid';

const props = defineProps({
    modelValue: { type: Number, default: 0 },
    maxStars: { type: Number, default: 10 },
    readOnly: { type: Boolean, default: false },
});

const emit = defineEmits(['update:modelValue']);

const rating = ref(props.modelValue);
const hoverRating = ref(0);

watch(() => props.modelValue, (newVal) => {
    rating.value = newVal;
});

function setRating(value) {
    if (props.readOnly) return;
    rating.value = value;
    emit('update:modelValue', value);
}

function setHoverRating(value) {
    if (props.readOnly) return;
    hoverRating.value = value;
}
</script>

<template>
    <div class="flex items-center" @mouseleave="setHoverRating(0)">
        <StarIcon v-for="star in maxStars" :key="star" class="h-6 w-6 cursor-pointer" :class="[
            (hoverRating || rating) >= star ? 'text-yellow-400' : 'text-gray-500',
            readOnly ? 'cursor-default' : ''
        ]" @click="setRating(star)" @mouseover="setHoverRating(star)" />
    </div>
</template>