<template>
    <div>
        <String
            label="AuthorEmail"
            v-model="value.authorEmail"
            :editMode="editMode"
        />
        <String
            label="AuthorName"
            v-model="value.authorName"
            :editMode="editMode"
        />
        <String
            label="Bio"
            v-model="value.bio"
            :editMode="editMode"
        />
        <String
            label="RepresentativeWork"
            v-model="value.representativeWork"
            :editMode="editMode"
        />
        <String
            label="Portfolio"
            v-model="value.portfolio"
            :editMode="editMode"
        />
        <Boolean
            label="IsApproved"
            v-model="value.isApproved"
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
    name: 'Author',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "authors",
        value: {
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
    },
}
</script>
