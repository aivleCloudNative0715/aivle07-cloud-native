<template>
    <v-container>
        <v-snackbar
            v-model="snackbar.status"
            :timeout="snackbar.timeout"
            :color="snackbar.color"
        >
            
            <v-btn style="margin-left: 80px;" text @click="snackbar.status = false">
                Close
            </v-btn>
        </v-snackbar>
        <div class="panel">
            <div class="gs-bundle-of-buttons" style="max-height:10vh;">
                <v-btn @click="addNewRow" @class="contrast-primary-text" small color="primary">
                    <v-icon small style="margin-left: -5px;">mdi-plus</v-icon>등록
                </v-btn>
                <v-btn :disabled="!selectedRow" style="margin-left: 5px;" @click="openEditDialog()" class="contrast-primary-text" small color="primary">
                    <v-icon small>mdi-pencil</v-icon>수정
                </v-btn>
                <v-btn style="margin-left: 5px;" @click="signUp" class="contrast-primary-text" small color="primary" :disabled="!hasRole('user')">
                    <v-icon small>mdi-minus-circle-outline</v-icon>회원가입
                </v-btn>
                <v-btn style="margin-left: 5px;" @click="requestSubscriptionDialog = true" class="contrast-primary-text" small color="primary" :disabled="!hasRole('user')">
                    <v-icon small>mdi-minus-circle-outline</v-icon>구독신청
                </v-btn>
                <v-dialog v-model="requestSubscriptionDialog" width="500">
                    <RequestSubscription
                        @closeDialog="requestSubscriptionDialog = false"
                        @requestSubscription="requestSubscription"
                    ></RequestSubscription>
                </v-dialog>
                <v-btn style="margin-left: 5px;" @click="requestContentAccessDialog = true" class="contrast-primary-text" small color="primary" >
                    <v-icon small>mdi-minus-circle-outline</v-icon>열람신청
                </v-btn>
                <v-dialog v-model="requestContentAccessDialog" width="500">
                    <RequestContentAccess
                        @closeDialog="requestContentAccessDialog = false"
                        @requestContentAccess="requestContentAccess"
                    ></RequestContentAccess>
                </v-dialog>
            </div>
            <ViewSubscriptionHistory @search="search" style="margin-bottom: 10px; background-color: #ffffff;"></ViewSubscriptionHistory>
            <ViewContectHistory @search="search" style="margin-bottom: 10px; background-color: #ffffff;"></ViewContectHistory>
            <div class="mb-5 text-lg font-bold"></div>
            <div class="table-responsive">
                <v-table>
                    <thead>
                        <tr>
                        <th>Id</th>
                        <th>Email</th>
                        <th>UserName</th>
                        <th>HasActiveSubscription</th>
                        <th>Message</th>
                        <th>SubscriptionDueDate</th>
                        <th>MyBookHistory</th>
                        <th>IsKt</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(val, idx) in value" 
                            @click="changeSelectedRow(val)"
                            :key="val"  
                            :style="val === selectedRow ? 'background-color: rgb(var(--v-theme-primary), 0.2) !important;':''"
                        >
                            <td class="font-semibold">{{ idx + 1 }}</td>
                            <td class="whitespace-nowrap" label="Email">{{ val.email }}</td>
                            <td class="whitespace-nowrap" label="UserName">{{ val.userName }}</td>
                            <td class="whitespace-nowrap" label="HasActiveSubscription">{{ val.hasActiveSubscription }}</td>
                            <td class="whitespace-nowrap" label="Message">{{ val.message }}</td>
                            <td class="whitespace-nowrap" label="SubscriptionDueDate">{{ val.subscriptionDueDate }}</td>
                            <td class="whitespace-nowrap" label="MyBookHistory">
                                <span v-for="(name, index) in val.myBookHistory" :key="index">
                                    {{ name }}<br>
                                </span>
                            </td>
                            <td class="whitespace-nowrap" label="IsKt">{{ val.isKt }}</td>
                            <v-row class="ma-0 pa-4 align-center">
                                <v-spacer></v-spacer>
                                <Icon style="cursor: pointer;" icon="mi:delete" @click="deleteRow(val)" />
                            </v-row>
                        </tr>
                    </tbody>
                </v-table>
            </div>
            <IntegerDetailGrid style="margin-top: 20px;" label="MyBookHistory" offline v-if="selectedRow" v-model="selectedRow.myBookHistory" :selectedRow="selectedRow"/>
        </div>
        <v-col>
            <v-dialog
                v-model="openDialog"
                transition="dialog-bottom-transition"
                width="35%"
            >
                <v-card>
                    <v-toolbar
                        color="primary"
                        class="elevation-0 pa-4"
                        height="50px"
                    >
                        <div style="color:white; font-size:17px; font-weight:700;">User 등록</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <User :offline="offline"
                            :isNew="!value.idx"
                            :editMode="true"
                            :inList="false"
                            v-model="newValue"
                            @add="append"
                        />
                    </v-card-text>
                </v-card>
            </v-dialog>
            <v-dialog
                v-model="editDialog"
                transition="dialog-bottom-transition"
                width="35%"
            >
                <v-card>
                    <v-toolbar
                        color="primary"
                        class="elevation-0 pa-4"
                        height="50px"
                    >
                        <div style="color:white; font-size:17px; font-weight:700;">User 수정</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <div>
                            <String label="Email" v-model="selectedRow.email" :editMode="true"/>
                            <String label="UserName" v-model="selectedRow.userName" :editMode="true"/>
                            <Boolean label="HasActiveSubscription" v-model="selectedRow.hasActiveSubscription" :editMode="true"/>
                            <String label="Message" v-model="selectedRow.message" :editMode="true"/>
                            <Number label="SubscriptionDueDate" v-model="selectedRow.subscriptionDueDate" :editMode="true"/>
                            < label="MyBookHistory" v-model="selectedRow.myBookHistory" :editMode="true"/>
                            <Boolean label="IsKt" v-model="selectedRow.isKt" :editMode="true"/>
                            <IntegerDetailGrid label="MyBookHistory" offline v-model="selectedRow.myBookHistory" :editMode="true"/>
                            <v-divider class="border-opacity-100 my-divider"></v-divider>
                            <v-layout row justify-end>
                                <v-btn
                                    width="64px"
                                    color="primary"
                                    @click="save"
                                >
                                    수정
                                </v-btn>
                            </v-layout>
                        </div>
                    </v-card-text>
                </v-card>
            </v-dialog>
        </v-col>
    </v-container>
</template>

<script>
import { ref } from 'vue';
import { useTheme } from 'vuetify';
import BaseGrid from '../base-ui/BaseGrid.vue'


export default {
    name: 'userGrid',
    mixins:[BaseGrid],
    components:{
    },
    data: () => ({
        path: 'users',
        requestSubscriptionDialog: false,
        requestContentAccessDialog: false,
    }),
    watch: {
    },
    methods:{
        async signUp(){
            try{
                var path = "signUp".toLowerCase();
                var temp = await this.repository.invoke(this.selectedRow, path, null)
                // 스넥바 관련 수정 필요
                // this.$EventBus.$emit('show-success','signUp 성공적으로 처리되었습니다.')
                for(var i = 0; i< this.value.length; i++){
                    if(this.value[i] == this.selectedRow){
                        this.value[i] = temp.data
                    }
                }
            }catch(e){
                console.log(e)
            }
        },
        async requestSubscription(params){
            try{
                var path = "requestSubscription".toLowerCase();
                var temp = await this.repository.invoke(this.selectedRow, path, params)
                // 스넥바 관련 수정 필요
                // this.$EventBus.$emit('show-success','requestSubscription 성공적으로 처리되었습니다.')
                for(var i = 0; i< this.value.length; i++){
                    if(this.value[i] == this.selectedRow){
                        this.value[i] = temp.data
                    }
                }
                this.requestSubscriptionDialog = false
            }catch(e){
                console.log(e)
            }
        },
        async requestContentAccess(params){
            try{
                var path = "requestContentAccess".toLowerCase();
                var temp = await this.repository.invoke(this.selectedRow, path, params)
                // 스넥바 관련 수정 필요
                // this.$EventBus.$emit('show-success','requestContentAccess 성공적으로 처리되었습니다.')
                for(var i = 0; i< this.value.length; i++){
                    if(this.value[i] == this.selectedRow){
                        this.value[i] = temp.data
                    }
                }
                this.requestContentAccessDialog = false
            }catch(e){
                console.log(e)
            }
        },
    }
}

</script>