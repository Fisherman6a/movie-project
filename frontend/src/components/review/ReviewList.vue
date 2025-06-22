<template>
    <n-list hoverable clickable>
      <n-list-item v-for="review in reviews" :key="review.id">
        <n-thing>
          <template #avatar>
            <n-avatar>{{ review.username?.[0].toUpperCase() }}</n-avatar>
          </template>
          <template #header>
            {{ review.username }}
          </template>
          <template #header-extra>
            <n-time :time="new Date(review.createdAt)" type="relative" />
          </template>
          {{ review.commentText }}
        </n-thing>
      </n-list-item>
      <n-empty v-if="!reviews || reviews.length === 0" description="暂无评论，快来抢占沙发吧！" />
    </n-list>
  </template>
  
  <script setup>
  import { NList, NListItem, NThing, NAvatar, NTime, NEmpty } from 'naive-ui';
  
  defineProps({
    reviews: {
      type: Array,
      required: true,
    },
  });
  </script>