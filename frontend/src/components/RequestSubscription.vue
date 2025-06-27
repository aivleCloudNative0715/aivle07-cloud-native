<template>

    <v-card outlined>
        <v-card-title>
            RequestSubscription
        </v-card-title>

        <v-card-text>
            <String label="Email" v-model="value.email" :editMode="editMode"/>
            <String label="UserName" v-model="value.userName" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="requestSubscription"
            >
                RequestSubscription
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
    name: 'RequestSubscriptionCommand',
    components:{
    },
    props: {},
    data: () => ({
        editMode: true,
        value: {},
    }),
    created() {
        this.value.email = '';
        this.value.userName = '';
    },
    watch: {
    },
    methods: {
        requestSubscription() {
            this.$emit('requestSubscription', this.value);
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

