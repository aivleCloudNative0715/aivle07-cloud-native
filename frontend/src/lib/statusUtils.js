export const getStatusLabel = (status) => {
    switch (status) {
        case "PUBLICATION_REQUESTED":
            return "출간 요청 완료";
        case "REGISTERED":
            return "임시 저장됨";
        case "AutoPublished":
            return "자동출판됨";
        default:
            return status ?? "-";
    }
};
