<template>

    <v-card outlined>
        <v-card-title>
            ManuscriptSave
        </v-card-title>

        <v-card-text>
            <Number label="Id" v-model="value.id" :editMode="editMode"/>
            <String label="AuthorId" v-model="value.authorId" :editMode="editMode"/>
            <String label="Title" v-model="value.title" :editMode="editMode"/>
            <String label="Content" v-model="value.content" :editMode="editMode"/>
            <String label="Status" v-model="value.status" :editMode="editMode"/>
            <Date label="LastModifiedAt" v-model="value.lastModifiedAt" :editMode="editMode"/>
            <String label="Summary" v-model="value.summary" :editMode="editMode"/>
            <String label="Keywords" v-model="value.keywords" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="manuscriptSave"
            >
                ManuscriptSave
            </v-btn>
            
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="close"
            >
                Close
            </v-btn>
        </v-card-actions>
    </v-card>

</template>

<script>

export default {
    name: 'ManuscriptSaveCommand',
    components:{
    },
    props: {},
    data: () => ({
        editMode: true,
        value: {},
    }),
    created() {
        this.value.id = 0;
        this.value.authorId = '';
        this.value.title = '';
        this.value.content = '';
        this.value.status = '';
        this.value.lastModifiedAt = '2025-06-27';
        this.value.summary = '';
        this.value.keywords = '';
    },
    watch: {
    },
    methods: {
        manuscriptSave() {
            this.$emit('manuscriptSave', this.value);
        },
        close() {
            this.$emit('closeDialog');
        },
        change() {
            this.$emit("update:modelValue", this.value);
        },
    }
}
</script>

