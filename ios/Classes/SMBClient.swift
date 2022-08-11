
import AMSMB2

class SMBClient {
    /// connect to: `smb://guest@XXX.XXX.XX.XX/share`
    
    let serverURL: URL
    let credential: URLCredential
    let share: String
    
    lazy private var client = AMSMB2(url: self.serverURL, credential: self.credential)!
    
    init(url: String, share: String, user: String, password: String) {
        serverURL = URL(string: url)!
        self.share = share
        credential = URLCredential(user: user, password: password, persistence: URLCredential.Persistence.forSession)
    }
    
    private func connect(handler: @escaping (Result<AMSMB2, Error>) -> Void) {
        client.connectShare(name: self.share) { error in
            if let error = error {
                handler(.failure(error))
            } else {
                handler(.success(self.client))
            }
        }
    }
    
    func listDirectory(path: String, handler: @escaping (Result<[String], Error>) -> Void) {
        connect { result in
            switch result {
            case .success(let client):
                client.contentsOfDirectory(atPath: path) { result in
                    switch result {
                    case .success(let files):
                        var shares: [String] = []
                        for entry in files {
                            shares.append(entry[.pathKey] as! String)
                        }
                        
                        handler(.success(shares))
                        
                    case .failure(let error):
                        handler(.failure(error))
                    }
                }
                
            case .failure(let error):
                handler(.failure(error))
            }
        }
        
    }
    
    
}