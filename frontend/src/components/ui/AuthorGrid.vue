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
                <v-btn style="margin-left: 5px;" @click="applyAuthor" class="contrast-primary-text" small color="primary" :disabled="!hasRole('author')">
                    <v-icon small>mdi-minus-circle-outline</v-icon>작가 신청
                </v-btn>
                <v-btn style="margin-left: 5px;" @click="judgeAuthor" class="contrast-primary-text" small color="primary" :disabled="!hasRole('administrator')">
                    <v-icon small>mdi-minus-circle-outline</v-icon>작가심사
                </v-btn>
            </div>
            <div class="mb-5 text-lg font-bold"></div>
            <div class="table-responsive">
                <v-table>
                    <thead>
                        <tr>
                        <th>Id</th>
                        <th>AuthorEmail</th>
                        <th>AuthorName</th>
                        <th>Bio</th>
                        <th>RepresentativeWork</th>
                        <th>Portfolio</th>
                        <th>IsApproved</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(val, idx) in value" 
                            @click="changeSelectedRow(val)"
                            :key="val"  
                            :style="val === selectedRow ? 'background-color: rgb(var(--v-theme-primary), 0.2) !important;':''"
                        >
                            <td class="font-semibold">{{ idx + 1 }}</td>
                            <td class="whitespace-nowrap" label="AuthorEmail">{{ val.authorEmail }}</td>
                            <td class="whitespace-nowrap" label="AuthorName">{{ val.authorName }}</td>
                            <td class="whitespace-nowrap" label="Bio">{{ val.bio }}</td>
                            <td class="whitespace-nowrap" label="RepresentativeWork">{{ val.representativeWork }}</td>
                            <td class="whitespace-nowrap" label="Portfolio">{{ val.portfolio }}</td>
                            <td class="whitespace-nowrap" label="IsApproved">{{ val.isApproved }}</td>
                            <v-row class="ma-0 pa-4 align-center">
                                <v-spacer></v-spacer>
                                <Icon style="cursor: pointer;" icon="mi:delete" @click="deleteRow(val)" />
                            </v-row>
                        </tr>
                    </tbody>
                </v-table>
            </div>
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
                        <div style="color:white; font-size:17px; font-weight:700;">Author 등록</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <Author :offline="offline"
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
                        <div style="color:white; font-size:17px; font-weight:700;">Author 수정</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <div>
                            <String label="AuthorEmail" v-model="selectedRow.authorEmail" :editMode="true"/>
                            <String label="AuthorName" v-model="selectedRow.authorName" :editMode="true"/>
                            <String label="Bio" v-model="selectedRow.bio" :editMode="true"/>
                            <String label="RepresentativeWork" v-model="selectedRow.representativeWork" :editMode="true"/>
                            <String label="Portfolio" v-model="selectedRow.portfolio" :editMode="true"/>
                            <Boolean label="IsApproved" v-model="selectedRow.isApproved" :editMode="true"/>
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
    name: 'authorGrid',
    mixins:[BaseGrid],
    components:{
    },
    data: () => ({
        path: 'authors',
    }),
    watch: {
    },
    methods:{
        async applyAuthor(){
            try{
                var path = "applyAuthor".toLowerCase();
                var temp = await this.repository.invoke(this.selectedRow, path, null)
                // 스넥바 관련 수정 필요
                // this.$EventBus.$emit('show-success','applyAuthor 성공적으로 처리되었습니다.')
                for(var i = 0; i< this.value.length; i++){
                    if(this.value[i] == this.selectedRow){
                        this.value[i] = temp.data
                    }
                }
            }catch(e){
                console.log(e)
            }
        },
        async judgeAuthor(){
            try{
                var path = "judgeAuthor".toLowerCase();
                var temp = await this.repository.invoke(this.selectedRow, path, null)
                // 스넥바 관련 수정 필요
                // this.$EventBus.$emit('show-success','judgeAuthor 성공적으로 처리되었습니다.')
                for(var i = 0; i< this.value.length; i++){
                    if(this.value[i] == this.selectedRow){
                        this.value[i] = temp.data
                    }
                }
            }catch(e){
                console.log(e)
            }
        },
    }
}

</script>