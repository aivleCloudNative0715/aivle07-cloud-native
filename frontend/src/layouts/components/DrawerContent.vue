<script setup>
import {
  VerticalNavLink,
  VerticalNavSectionTitle,
} from '@layouts'
</script>

<template>
    <ul>
        <VerticalNavSectionTitle :item="{ heading: '사용자 관리' }" />
        <VerticalNavLink
            :item="{
                title: '사용자',
                to: '/users',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '구독내역조회',
                to: '/viewSubscriptionHistories',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '열람내역조회',
                to: '/viewReadingHistories',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '구독내역조회',
                to: '/viewSubscriptionHistories',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '열람기록조회',
                to: '/viewContectHistories',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: '작가관리' }" />
        <VerticalNavLink
            :item="{
                title: '작가관리',
                to: '/authors',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '출간진행상황',
                to: '/publicationStatuses',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: 'AI 서비스' }" />
        <VerticalNavLink
            :item="{
                title: '책작업관리',
                to: '/bookWorks',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: '서재플랫폼' }" />
        <VerticalNavLink
            :item="{
                title: '책',
                to: '/books',
            }"
        />
        <VerticalNavLink
            :item="{
                title: 'BookView',
                to: '/bookViews',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: '집필관리' }" />
        <VerticalNavLink
            :item="{
                title: '원고',
                to: '/manuscripts',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '원고조회',
                to: '/manuscriptLists',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: '포인트' }" />
        <VerticalNavLink
            :item="{
                title: 'Point',
                to: '/points',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '포인트 조회',
                to: '/pointInquiries',
            }"
        />
    </ul>
</template>
