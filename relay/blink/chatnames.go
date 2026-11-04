package blink

import (
	_ "embed"
	"math/rand/v2"
	"strings"
)

//go:embed chat-names.txt
var chatNamesFile string

var chatNames = strings.Split(strings.TrimSpace(chatNamesFile), "\n")

func RandomChatName() string {
	return chatNames[rand.IntN(len(chatNames))]
}
