<template>
    <div>
        <String
            label="Email"
            v-model="value.email"
            :editMode="editMode"
        />
        <String
            label="UserName"
            v-model="value.userName"
            :editMode="editMode"
        />
        <Boolean
            label="HasActiveSubscription"
            v-model="value.hasActiveSubscription"
            :editMode="editMode"
        />
        <String
            label="Message"
            v-model="value.message"
            :editMode="editMode"
        />
        <Number
            label="SubscriptionDueDate"
            v-model="value.subscriptionDueDate"
            :editMode="editMode"
        />
        <v-divider class="border-opacity-50 my-divider my-2"></v-divider>
        <div variant="outlined" class="my-2">
            <Number
                label="MyBookHistory"
                v-model="myBookHistoryInput"
                :editMode="editMode"
                @keydown.enter="addMyBookHistoryList"
            />
            <v-card v-if="value.myBookHistory.length > 0" variant="outlined" class="pa-4 mt-2">
                <v-card-sub-title>
                    MyBookHistory List
                </v-card-sub-title>
                <li v-for="(name, index) in value.myBookHistory" :key="index">
                    {{ name }}
                </li>
            </v-card>
            <v-row class="ma-0 pa-0 mt-2">
                <v-spacer></v-spacer>
                <v-btn @click="addMyBookHistoryList">
                    MyBookHistory 추가
                </v-btn>
            </v-row>
            
            <IntegerDetailGrid
                label="MyBookHistory"
                offline
                v-model="value.myBookHistory"
                :editMode="editMode"
                @change="change"
            />
        </div>
        <v-divider class="border-opacity-50 my-divider my-2"></v-divider>
        <Boolean
            label="IsKt"
            v-model="value.isKt"
            :editMode="editMode"
        />
        <v-row class="ma-0 pa-0">
            <v-spacer></v-spacer>
            <v-btn width="64px" color="primary" @click="save">
                저장
            </v-btn>
        </v-row>
    </div>
</template>


<script>
import BaseEntity from './base-ui/BaseEntity.vue'

export default {
    name: 'User',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "users",
        myBookHistoryInput: null,
        value: {
            myBookHistory: [],
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
        addMyBookHistoryList() {
            if (this.myBookHistoryInput !== null && this.myBookHistoryInput !== '') {
                this.value.myBookHistory.push(this.myBookHistoryInput);
                this.myBookHistoryInput = null; // null로 초기화
            }
        },
    },
}
</script>
