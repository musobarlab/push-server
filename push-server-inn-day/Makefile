.PHONY : test build clean format

build:
	go build github.com/telkomdev/push-server-inn-day/cmd/push-server

test:
	go test ./...

format:
	find . -name "*.go" -not -path "./vendor/*" -not -path ".git/*" | xargs gofmt -s -d -w

clean:
	rm push-server