export function cn(...classes) {
    return classes
        .filter(Boolean)
        .map((c) => (typeof c === "string" ? c : ""))
        .join(" ");
}
